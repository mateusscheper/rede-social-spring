package scheper.mateus.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import scheper.mateus.dto.ReportDTO;
import scheper.mateus.entity.Comentario;
import scheper.mateus.entity.Post;
import scheper.mateus.entity.Report;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;
import scheper.mateus.repository.ComentarioRepository;
import scheper.mateus.repository.PostRepository;
import scheper.mateus.repository.ReportRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AdminService {

    private final JwtService jwtService;

    private final PostRepository postRepository;

    private final ComentarioRepository comentarioRepository;

    private final ReportRepository reportRepository;

    public AdminService(JwtService jwtService, PostRepository postRepository, ComentarioRepository comentarioRepository, ReportRepository reportRepository) {
        this.jwtService = jwtService;
        this.postRepository = postRepository;
        this.comentarioRepository = comentarioRepository;
        this.reportRepository = reportRepository;
    }

    public void reportar(ReportDTO reportDTO, HttpServletRequest request) {
        Usuario usuario = jwtService.obterUsuarioDoRequest(request);
        Post post = null;
        Comentario comentario = null;

        if ("post".equals(reportDTO.getTipo()))
            post = postRepository.getById(reportDTO.getId());
        else if ("comentario".equals(reportDTO.getTipo()))
            comentario = comentarioRepository.getById(reportDTO.getId());

        if (existeReportAberto(post, comentario))
            throw new BusinessException("{report.validacao.existeReportAberto}");

        Report report = new Report(reportDTO, usuario, post, comentario);

        reportRepository.save(report);
    }

    private boolean existeReportAberto(Post post, Comentario comentario) {
        Long idPostOuComentario;
        if (post != null)
            idPostOuComentario = post.getIdPost();
        else if (comentario != null)
            idPostOuComentario = comentario.getIdComentario();
        else
            return false;

        return reportRepository.existeReportAberto(idPostOuComentario);
    }

    public List<ReportDTO> consultarReports() {
        return reportRepository.findAll(Sort.by("idReport"))
                .stream()
                .map(ReportDTO::new)
                .toList();
    }
}