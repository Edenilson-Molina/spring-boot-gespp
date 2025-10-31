package com.template.spring_mvc.config;

import com.template.spring_mvc.model.*;
import com.template.spring_mvc.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FacultadRepository facultadRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SupervisorRepository supervisorRepository;

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Override
    public void run(String... args) throws Exception {
        // Semillas de Facultades (solo si no hay registros)
        if (facultadRepository.count() == 0) {
            Facultad f1 = new Facultad();
            f1.setNombre("Ingeniería y Arquitectura");
            f1.setActivo(true);

            Facultad f2 = new Facultad();
            f2.setNombre("Ciencias Económicas");
            f2.setActivo(true);

            Facultad f3 = new Facultad();
            f3.setNombre("Humanidades");
            f3.setActivo(true);

            facultadRepository.save(f1);
            facultadRepository.save(f2);
            facultadRepository.save(f3);
        }

        // Semillas de Carreras (si no hay ninguna)
        if (carreraRepository.count() == 0) {
            facultadRepository.findByNombre("Ingeniería y Arquitectura").ifPresent(f -> {
                if (!carreraRepository.existsByNombreAndFacultadId("Ingeniería de Sistemas", f.getId())) {
                    Carrera c1 = new Carrera();
                    c1.setNombre("Ingeniería de Sistemas");
                    c1.setActivo(true);
                    c1.setFacultad(f);
                    carreraRepository.save(c1);
                }
                if (!carreraRepository.existsByNombreAndFacultadId("Arquitectura", f.getId())) {
                    Carrera c2 = new Carrera();
                    c2.setNombre("Arquitectura");
                    c2.setActivo(true);
                    c2.setFacultad(f);
                    carreraRepository.save(c2);
                }
            });
            facultadRepository.findByNombre("Ciencias Económicas").ifPresent(f -> {
                if (!carreraRepository.existsByNombreAndFacultadId("Administración de Empresas", f.getId())) {
                    Carrera c3 = new Carrera();
                    c3.setNombre("Administración de Empresas");
                    c3.setActivo(true);
                    c3.setFacultad(f);
                    carreraRepository.save(c3);
                }
            });
        }

        // Crear/asegurar permiso
        Permission readPerm = permissionRepository.findByName("READ_USER");
        if (readPerm == null) {
            readPerm = new Permission();
            readPerm.setName("READ_USER");
            permissionRepository.save(readPerm);
        }

        // Crear/asegurar roles
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
        }
        if (!adminRole.getPermissions().contains(readPerm)) {
            adminRole.getPermissions().add(readPerm);
        }
        adminRole = roleRepository.save(adminRole);

        Role userRole = roleRepository.findByName("ROLE_ESTUDIANTE");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_ESTUDIANTE");
            userRole = roleRepository.save(userRole);
        }

        // Crear/asegurar usuarios
        User admin = userRepository.findByEmail("admin@example.com").orElse(null);
        if (admin == null) {
            admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("Admin");
            admin.setApellidos("Root");
        }
        if (!admin.getRoles().contains(adminRole)) {
            admin.getRoles().add(adminRole);
        }
        admin = userRepository.save(admin);

        User student1 = userRepository.findByEmail("student_1@example.com").orElse(null);
        if (student1 == null) {
            student1 = new User();
            student1.setEmail("student_1@example.com");
            student1.setPassword(passwordEncoder.encode("student123"));
            student1.setName("Student");
            student1.setApellidos("One");
        }
        if (!student1.getRoles().contains(userRole)) {
            student1.getRoles().add(userRole);
        }
        student1 = userRepository.save(student1);

        // Crear/asegurar Estudiante asociado a student_1
        if (estudianteRepository.findByUserEmail("student_1@example.com").isEmpty()) {
            Estudiante est = new Estudiante();
            est.setCarnet("CARNET001");
            est.setFechaNacimiento(LocalDate.of(2002, 1, 1));
            est.setDui("123456789"); // 9 dígitos, sin guión
            est.setActivo(true);
            est.setUser(student1);
            estudianteRepository.save(est);
        }

        // Crear/asegurar Empresas
        Empresa emp1 = empresaRepository.findByNombre("TechCorp").orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNombre("TechCorp");
            e.setActivo(true);
            return empresaRepository.save(e);
        });
        Empresa emp2 = empresaRepository.findByNombre("BizSolutions").orElseGet(() -> {
            Empresa e = new Empresa();
            e.setNombre("BizSolutions");
            e.setActivo(true);
            return empresaRepository.save(e);
        });

        // Crear/asegurar Supervisores (1 por empresa)
        Supervisor sup1;
        List<Supervisor> supEmp1 = supervisorRepository.findByEmpresa(emp1);
        if (supEmp1.isEmpty()) {
            sup1 = new Supervisor();
            sup1.setNombre("Laura");
            sup1.setApellido("García");
            sup1.setDescripcion("Supervisora de proyectos de software");
            sup1.setActivo(true);
            sup1.setEmpresa(emp1);
            sup1 = supervisorRepository.save(sup1);
        } else {
            sup1 = supEmp1.get(0);
        }

        Supervisor sup2;
        List<Supervisor> supEmp2 = supervisorRepository.findByEmpresa(emp2);
        if (supEmp2.isEmpty()) {
            sup2 = new Supervisor();
            sup2.setNombre("Carlos");
            sup2.setApellido("Pérez");
            sup2.setDescripcion("Supervisor del área de operaciones");
            sup2.setActivo(true);
            sup2.setEmpresa(emp2);
            sup2 = supervisorRepository.save(sup2);
        } else {
            sup2 = supEmp2.get(0);
        }

        // Crear/asegurar Ofertas (2 por supervisor => 4 en total)
        if (ofertaRepository.findBySupervisor(sup1).isEmpty()) {
            Oferta o1 = new Oferta();
            o1.setNombre("Desarrollador Junior");
            o1.setDescripcion("Apoyo en desarrollo de aplicaciones web");
            o1.setVacantes(5);
            o1.setOcupados(0);
            o1.setActivo(true);
            o1.setSupervisor(sup1);
            ofertaRepository.save(o1);

            Oferta o2 = new Oferta();
            o2.setNombre("QA Assistant");
            o2.setDescripcion("Pruebas manuales y documentación");
            o2.setVacantes(3);
            o2.setOcupados(0);
            o2.setActivo(true);
            o2.setSupervisor(sup1);
            ofertaRepository.save(o2);
        }

        if (ofertaRepository.findBySupervisor(sup2).isEmpty()) {
            Oferta o3 = new Oferta();
            o3.setNombre("Analista de Datos Jr");
            o3.setDescripcion("Análisis básico y reportes");
            o3.setVacantes(4);
            o3.setOcupados(0);
            o3.setActivo(true);
            o3.setSupervisor(sup2);
            ofertaRepository.save(o3);

            Oferta o4 = new Oferta();
            o4.setNombre("Asistente de Operaciones");
            o4.setDescripcion("Soporte en logística y coordinación");
            o4.setVacantes(2);
            o4.setOcupados(0);
            o4.setActivo(true);
            o4.setSupervisor(sup2);
            ofertaRepository.save(o4);
        }
    }
}