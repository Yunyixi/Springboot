package chapter.project.chapter05.file;

import org.apache.commons.io.FileUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Name FeiLong
 * @Date 2023/9/18
 * @注释 文件控制管理类 文件的上传与下载
 */
@Controller
public class FileController {
    // 5.3.1文件上传
    // 向文件上传页面跳转，页面映射
    @GetMapping("/filetoUpload") //http://localhost:8084/toUpload
    public String toUpload() {
        return "fileupload";
    }

    // 文件上传
    @PostMapping("/uploadFile")
    public String uploadFile(MultipartFile[] fileUpload, Model model) {
        // 默认文件上传成功，并返回信息状态
        model.addAttribute("uploadStatus", "上传成功！");
        for (MultipartFile file : fileUpload) {
            // 获取文件名以及后缀名
            String fileName = file.getOriginalFilename();
            // 重新生成文件名
            fileName = UUID.randomUUID() + "_" + fileName;
            // 指定上传文件本地储存目录，不存在则需要提前创建
            String dirPath = "D:/Recording/SpringBootData/";
            File filePath = new File(dirPath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            try {
                file.transferTo(new File(dirPath + fileName));
            } catch (Exception e) {
                e.printStackTrace();
                // 上传失败，返回失败信息
                model.addAttribute("uploadStatus", "上传失败： " + e.getMessage());
            }
        }
        // 携带上传状态信息回调到文件上传页面
        return "fileupload";
    }

    // 5.3.2文件下载
    // 向文件下载页面跳转，页面映射
    @GetMapping("/toDownload") //http://localhost:8084/toDownload
    public String toDownload() {
        return "download";
    }

//    // 文件下载管理
//    @GetMapping("/download")
//    public ResponseEntity<byte[]> fileDownload(String filename){
//        // 指定要下载的文件根路径
//        String dirPath = "D:/Recording/SpringBootData/";
//        // 创建该文件对象
//        File file = new File(dirPath + File.separator + filename);
//        // 设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        // 通知浏览器以下载方式打开
//        headers.setContentDispositionFormData("attachment",filename);
//        // 定义以流的形式下载返回文件数据
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        try {
//            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<byte[]>(e.getMessage().getBytes(),HttpStatus.EXPECTATION_FAILED);
//        }
//    }

    //浏览器适配，所有文件下载
    // 所有类型文件下载管理
    @GetMapping("/download")
    public ResponseEntity<byte[]> fileDownload(HttpServletRequest request,
                                               String filename) throws Exception {
        // 指定要下载的文件根路径
        String dirPath = "D:/Recording/SpringBootData/";
        // 创建该文件对象
        File file = new File(dirPath + File.separator + filename);
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        // 通知浏览器以下载方式打开（下载前对文件名进行转码）
        filename = getFilename(request, filename);
        headers.setContentDispositionFormData("attachment", filename);
        // 定义以流的形式下载返回文件数据
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            return new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<byte[]>(e.getMessage().getBytes(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    // 浏览器适配，根据浏览器的不同进行编码设置，返回编码后的文件名
    private String getFilename(HttpServletRequest request, String filename)
            throws Exception {
        // IE不同版本User-Agent中出现的关键词
        String[] IEBrowserKeyWords = {"MSIE", "Trident", "Edge"};
        // 获取请求头代理信息
        String userAgent = request.getHeader("User-Agent");
        for (String keyWord : IEBrowserKeyWords) {
            if (userAgent.contains(keyWord)) {
                //IE内核浏览器，统一为UTF-8编码显示，并对转换的+进行更正
                return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", " ");
            }
        }
        //火狐等其它浏览器统一为ISO-8859-1编码显示
        return new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
    }
}
