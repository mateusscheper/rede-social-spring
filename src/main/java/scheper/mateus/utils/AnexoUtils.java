package scheper.mateus.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import scheper.mateus.entity.Arquivo;
import scheper.mateus.entity.Usuario;
import scheper.mateus.exception.BusinessException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AnexoUtils {

    private AnexoUtils() {
    }

    public static Arquivo uparImagemServidorComCropECriarArquivo(MultipartFile imagem, Usuario criador, String diretorio) {
        validarTipoArquivo(imagem);
        String nomeArquivoCodificado = getNomeArquivoCodificado(imagem, criador);
        String nomeReal = imagem.getOriginalFilename() != null ? imagem.getOriginalFilename() : imagem.getName();
        File arquivoUpado = uparArquivoServidor(imagem, null, nomeArquivoCodificado, criador.getIdUsuario(), diretorio);
        File imagemMiniatura = gerarMiniatura(criador, diretorio, nomeArquivoCodificado, arquivoUpado);

        return criarArquivo(arquivoUpado, criador, nomeReal, nomeArquivoCodificado, imagemMiniatura != null && imagemMiniatura.exists(), diretorio);
    }

    private static File gerarMiniatura(Usuario criador, String diretorio, String nomeArquivoCodificado, File arquivoUpado) {
        File imagemCropada;
        try {
            BufferedImage imagemParaMiniatura = ImageIO.read(arquivoUpado);
            Image imagemEmEscala = imagemParaMiniatura.getScaledInstance(36, 36, Image.SCALE_DEFAULT);
            BufferedImage imagemMiniatura = new BufferedImage(36, 36, BufferedImage.TYPE_INT_RGB);
            imagemMiniatura.getGraphics().drawImage(imagemEmEscala, 0, 0, null);
            imagemCropada = uparArquivoServidor(null, imagemMiniatura, concatenarNomeImagemMiniatura(nomeArquivoCodificado), criador.getIdUsuario(), diretorio);
        } catch (IOException e) {
            return null;
        }
        return imagemCropada;
    }

    private static String concatenarNomeImagemMiniatura(String nomeArquivoCodificado) {
        String extensao = nomeArquivoCodificado.substring(nomeArquivoCodificado.lastIndexOf("."));
        return nomeArquivoCodificado.replace(extensao, "-36x36" + extensao);
    }

    private static void validarTipoArquivo(MultipartFile arquivo) {
        if (arquivo != null) {
            String nomeArquivo = arquivo.getOriginalFilename();
            String extensao = nomeArquivo != null ? nomeArquivo.split("\\.")[1] : null;

            if (isNotImagem(extensao))
                throw new BusinessException("{post.validacao.arquivoNaoImagem}");
        }
    }

    private static boolean isNotImagem(String extensao) {
        if (ObjectUtils.isEmpty(extensao))
            return true;

        List<String> tiposImagens = new ArrayList<>();
        tiposImagens.add("jpg");
        tiposImagens.add("jpeg");
        tiposImagens.add("bmp");
        tiposImagens.add("png");

        return !tiposImagens.contains(extensao.toLowerCase().trim());
    }

    private static String getNomeArquivoCodificado(MultipartFile imagem, Usuario criador) {
        String nome = codificarNome(criador.getIdUsuario()) + ".";
        if (imagem.getOriginalFilename() != null)
            return nome.concat(imagem.getOriginalFilename().substring(imagem.getOriginalFilename().lastIndexOf(".") + 1));
        else
            return nome.concat(imagem.getName().substring(imagem.getName().lastIndexOf(".") + 1));
    }

    private static String codificarNome(Long idUsuario) {
        return idUsuario.toString() + RandomStringUtils.randomAlphanumeric(30);
    }

    private static File uparArquivoServidor(MultipartFile arquivo, BufferedImage arquivoCropado, String nomeArquivoCodificado, Long idUsuario, String diretorio) {
        try {
            String pastaUpload = System.getProperty("user.home") + separator() + "uploads" + separator() + diretorio + separator();

            if (!new File(pastaUpload).exists())
                new File(pastaUpload).mkdirs();

            File arquivoReal = criarArquivoFisico(nomeArquivoCodificado, pastaUpload, idUsuario);

            if (arquivo != null) {
                arquivo.transferTo(arquivoReal);
            } else if (arquivoCropado != null) {
                ImageIO.write(arquivoCropado, obterExtensao(nomeArquivoCodificado), arquivoReal);
            }

            return arquivoReal;
        } catch (IOException e) {
            throw new BusinessException("Arquivo inválido.");
        }
    }

    private static String obterExtensao(String nomeArquivoCodificado) {
        return nomeArquivoCodificado.substring(nomeArquivoCodificado.lastIndexOf(".") + 1);
    }

    private static String separator() {
        String separator = System.getProperty("file.separator");
        if (StringUtils.isEmpty(separator))
            return "/";
        return separator;
    }

    private static File criarArquivoFisico(String nomeArquivoCodificado, String pastaUpload, Long idUsuario) {
        String caminhoCompletoArquivo = pastaUpload + nomeArquivoCodificado;
        File arquivoReal = new File(caminhoCompletoArquivo);
        if (arquivoReal.exists()) {
            nomeArquivoCodificado = codificarNome(idUsuario);
            criarArquivoFisico(nomeArquivoCodificado, pastaUpload, idUsuario);
        }
        return arquivoReal;
    }

    private static Arquivo criarArquivo(File imagem, Usuario usuario, String nomeReal, String nomeCodificado, boolean possuiImagemMiniatura, String diretorio) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setDono(usuario);
            arquivo.setNomeReal(nomeReal);
            arquivo.setNomeCodificado(nomeCodificado);
            arquivo.setCaminho(obterCaminhoUpload(nomeCodificado, diretorio));
            arquivo.setTipo(nomeReal.substring(nomeReal.lastIndexOf(".") + 1));
            arquivo.setTamanho(Files.size(Path.of(imagem.getAbsolutePath())));

            if (possuiImagemMiniatura)
                arquivo.setCaminhoCrop(obterCaminhoUpload(concatenarNomeImagemMiniatura(nomeCodificado), diretorio));

            return arquivo;
        } catch (IOException e) {
            throw new BusinessException("Arquivo inválido.");
        }
    }

    private static String obterCaminhoUpload(String nomeCodificado, String diretorio) {
        return "assets" + separator() + "uploads" + separator() + diretorio + separator() + nomeCodificado;
    }

    @SuppressWarnings({"java:S4042", "java:S899"})
    public static void excluirArquivoServidor(Arquivo arquivo) {
        File file = new File(arquivo.getCaminho());
        if (file.exists())
            file.delete();
    }
}