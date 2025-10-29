package com.template.spring_mvc.controller;

import com.template.spring_mvc.dto.RegistroEstudianteDto;
import com.template.spring_mvc.model.Estudiante;
import com.template.spring_mvc.model.Role;
import com.template.spring_mvc.model.User;
import com.template.spring_mvc.repository.RoleRepository;
import com.template.spring_mvc.repository.UserRepository;
import com.template.spring_mvc.service.EstudianteService;
import com.template.spring_mvc.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Controller
@RequestMapping("/expediente/estudiante")
public class EstudianteRegistroController {

    private final EstudianteService estudianteService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public EstudianteRegistroController(EstudianteService estudianteService,
                                        UserRepository userRepository,
                                        RoleRepository roleRepository,
                                        PasswordEncoder passwordEncoder,
                                        UserService userService) {
        this.estudianteService = estudianteService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @GetMapping("/registro")
    public String mostrarFormulario(Model model) {
        model.addAttribute("registro", new RegistroEstudianteDto());
        return "expediente/estudiante/form";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registro") RegistroEstudianteDto registro,
                            BindingResult result,
                            Model model,
                            HttpServletRequest request) {
        // Validaciones personalizadas
        if (!registro.getPassword().equals(registro.getConfirmPassword())) {
            result.addError(new FieldError("registro", "confirmPassword", "Las contraseñas no coinciden"));
        }

        if (userRepository.findByEmail(registro.getEmail()).isPresent()) {
            result.addError(new FieldError("registro", "email", "El correo ya está registrado"));
        }

        if (estudianteService.existsByCarnet(registro.getCarnet())) {
            result.addError(new FieldError("registro", "carnet", "El carnet ya está registrado"));
        }

        LocalDate fn = registro.getFechaNacimiento();
        if (fn != null) {
            int edad = Period.between(fn, LocalDate.now()).getYears();
            if (edad >= 18) {
                if (registro.getDui() == null || registro.getDui().isBlank()) {
                    result.addError(new FieldError("registro", "dui", "El DUI es obligatorio para mayores de edad"));
                }
            }
        }

        if (registro.getDui() != null && !registro.getDui().isBlank()) {
            if (estudianteService.existsByDui(registro.getDui())) {
                result.addError(new FieldError("registro", "dui", "El DUI ya está registrado"));
            }
        }

        if (result.hasErrors()) {
            return "expediente/estudiante/form";
        }

        // Crear usuario
        User user = new User();
        user.setName(registro.getNombres());
        user.setApellidos(registro.getApellidos());
        user.setEmail(registro.getEmail());
        user.setPassword(passwordEncoder.encode(registro.getPassword()));

        Role rolEstudiante = roleRepository.findByName("ROLE_ESTUDIANTE");
        if (rolEstudiante != null) {
            user.getRoles().add(rolEstudiante);
        }
        userRepository.save(user);

        // Crear estudiante
        Estudiante estudiante = new Estudiante();
        estudiante.setCarnet(registro.getCarnet());
        estudiante.setFechaNacimiento(registro.getFechaNacimiento());
        estudiante.setDui((registro.getDui() != null && !registro.getDui().isBlank()) ? registro.getDui() : null);
        estudiante.setActivo(true);
        estudiante.setUser(user);
        estudianteService.save(estudiante);

        // Auto-login del usuario recién registrado
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession(true); // Asegura persistencia en sesión

        return "redirect:/";
    }
}
