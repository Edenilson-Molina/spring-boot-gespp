package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.repository.projection.ActivosEmpresaRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
        List<Expediente> findByEstudianteId(Long estudianteId);
        boolean existsByEstudianteIdAndCarreraId(Long estudianteId, Long carreraId);

        @Query("""
                        select s.empresa.id as empresaId,
                                     s.empresa.nombre as empresaNombre,
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
        List<ActivosEmpresaRow> countActivosPorEmpresa(@Param("start") LocalDate start,
                                                                                                     @Param("end") LocalDate end);
}
