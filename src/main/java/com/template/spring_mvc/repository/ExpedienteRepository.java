package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.repository.projection.ActivosEmpresaRow;
import com.template.spring_mvc.repository.projection.CarreraEstadoRow;
import com.template.spring_mvc.repository.projection.EmpresaEstadoRow;
import com.template.spring_mvc.repository.projection.EstadoRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {

    List<Expediente> findByEstudianteId(Long estudianteId);

    boolean existsByEstudianteIdAndCarreraId(Long estudianteId, Long carreraId);

    @Query("""
        select s.empresa.id      as empresaId,
               s.empresa.nombre  as empresaNombre,
               count(e)          as totalActivos
        from Expediente e
        join e.oferta o
        join o.supervisor s
        where e.estado = 'En curso'
          and e.fechaInicio <= :start
          and e.fechaFin    >= :end
        group by s.empresa.id, s.empresa.nombre
        order by totalActivos desc
        """)
    List<ActivosEmpresaRow> countActivosPorEmpresa(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("""
        select c.id      as carreraId,
               c.nombre  as carreraNombre,
               e.estado  as estado,
               count(e)  as total
        from Expediente e
        join e.carrera c
        where e.fechaInicio <= :start
          and e.fechaFin    >= :end
        group by c.id, c.nombre, e.estado
        order by c.nombre asc, e.estado asc
        """)
    List<CarreraEstadoRow> countPorCarreraYEstado(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("""
        select emp.id     as empresaId,
               emp.nombre as empresaNombre,
               e.estado   as estado,
               count(e)   as total
        from Expediente e
        join e.oferta o
        join o.supervisor s
        join s.empresa emp
        where e.fechaInicio <= :start
          and e.fechaFin    >= :end
        group by emp.id, emp.nombre, e.estado
        order by emp.nombre asc, e.estado asc
        """)
    List<EmpresaEstadoRow> countPorEmpresaYEstado(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );

    @Query("""
        select e.estado as estado,
               count(e)  as total
        from Expediente e
        where e.fechaInicio <= :start
          and e.fechaFin    >= :end
        group by e.estado
        order by e.estado asc
        """)
    List<EstadoRow> countPorEstado(
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
