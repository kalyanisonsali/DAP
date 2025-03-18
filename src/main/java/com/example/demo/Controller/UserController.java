package com.example.demo.Controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Model.PasswordResetToken;
import com.example.demo.Model.User;
import com.example.demo.Repo.PasswordResetTokenRepository;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.UserService;

@Controller
public class UserController {
    @Autowired
    UserService userservice;

    @Autowired
    UserRepo ur;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;
    
    

    // ----------------------- Authentication -----------------------

    @GetMapping("/")
    public String signIn(HttpSession session) {
        if (session.getAttribute("loggedInUser") != null) {
            return "login";
        }
        session.removeAttribute("success");
        session.removeAttribute("error");
        return "login";
    }

    @GetMapping("/signup")
    public String signUp() {
        return "signup";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    
    
    @PostMapping("/checkdata")
    public String checkLogin(@RequestParam("email") String email, @RequestParam("password") String password,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        Integer loginAttempts = (Integer) session.getAttribute("loginAttempts");
        if (loginAttempts == null) loginAttempts = 0;

        if (loginAttempts >= 5) {
            redirectAttributes.addFlashAttribute("error", "Too many failed login attempts. Try again later.");
            return "redirect:/";
        }

        Optional<User> userOptional = ur.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) { // TODO: Use encrypted password check
                session.setAttribute("loggedInUser", user);
                session.setAttribute("loginAttempts", 0);
                redirectAttributes.addFlashAttribute("success", "Login successful!");
                return "redirect:/userDash";
            }
        }
        session.setAttribute("loginAttempts", loginAttempts + 1);
        redirectAttributes.addFlashAttribute("error", "Invalid email or password.");
        return "redirect:/";
    }

    // ----------------------- User Dashboard -----------------------

    @GetMapping("/userDash")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/";
        }

        model.addAttribute("fname", user.getFname());
        model.addAttribute("lname", user.getLname());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("company", user.getCompany());
        model.addAttribute("currentPage", "userDash");
        model.addAttribute("picture", user.getProfileImage()); // Ensure this returns a valid URL


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("lastLogin", user.getLastLogin() != null ? user.getLastLogin().format(formatter) : "Never");

        // Handle profile image
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            model.addAttribute("profileImage", "/profile_pictures/" + user.getProfileImage());
        } else {
            model.addAttribute("profileImage", "/images/default-profile.png");
        }

        return "dash";
    }

    // ----------------------- Profile Update -----------------------

    @PostMapping("/update-profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam("fname") String fname,
            @RequestParam("lname") String lname,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("status", "error");
            response.put("message", "User not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        user.setFname(fname);
        user.setLname(lname);

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            user.setPassword(newPassword); // TODO: Encrypt password before saving
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/profile_pictures/");

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                user.setProfileImage(fileName);

                // Construct image URL
                imageUrl = "/uploads/profile_pictures/" + fileName;

            } catch (IOException e) {
                e.printStackTrace();
                response.put("status", "error");
                response.put("message", "Error uploading image.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }

        ur.save(user);
        session.setAttribute("loggedInUser", user);

        // Return success response with updated data
        response.put("status", "success");
//        response.put("fname", user.getFname());
//        response.put("lname", user.getLname());
//        response.put("profileImage", imageUrl != null ? imageUrl : "/images/default-profile.png");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/uploads/profile_pictures/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) {
        Path filePath = Paths.get("uploads/profile_pictures/").resolve(fileName);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    // ----------------------- Email Validation -----------------------

    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, String> checkEmail(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        response.put("status", userservice.isEmailExists(email) ? "exists" : "available");
        return response;
    }

   
    // ----------------------- User Registration -----------------------

    @PostMapping("/savedata")
    @ResponseBody
    public Map<String, String> saveUser(@RequestParam String fname, @RequestParam String lname,
                                        @RequestParam String email, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();

        if (userservice.isEmailExists(email)) {
            response.put("status", "error");
            response.put("message", "Email already exists");
            return response;
        }

        User user = new User();
        user.setFname(fname);
        user.setLname(lname);
        user.setEmail(email);
        user.setPassword(password); // TODO: Encrypt before saving

        userservice.saveUser(user);
        response.put("status", "success");
        return response;
    }
    
    @GetMapping("/getCompany")
    public ResponseEntity<?> getCompany(@RequestParam String email) {
        Optional<User> user = ur.findByEmail(email);
        if (user.isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("company", user.get().getCompany());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found");
    }
    // ----------------------- Forgot Password -----------------------

    // Show Forgot Password Page
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    // Handle Forgot Password Form Submission
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOptional = userservice.findByEmail(email);
        if (userOptional.isPresent()) {
            String token = UUID.randomUUID().toString();
            userservice.createPasswordResetToken(userOptional.get(), token);

            String resetUrl = "http://localhost:8080/reset-password?token=" + token;
            emailService.sendResetPasswordEmail(email, resetUrl);

            model.addAttribute("message", "A password reset link has been sent to your email.");
        } else {
            model.addAttribute("error", "Email not found!");
        }
        return "forgot-password";
    }

    // Show Reset Password Page
    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        Optional<User> userOptional = userservice.findByToken(token);
        if (userOptional.isPresent()) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "forgot-password";
        }
    }

    // Handle Reset Password Form Submission
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String password, Model model) {
        Optional<User> userOptional = userservice.findByToken(token);
        if (userOptional.isPresent()) {
            userservice.updatePassword(userOptional.get(), password);
            model.addAttribute("message", "Password updated successfully. You can now log in.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            return "reset-password";
        }
    }
}
    
   
    

