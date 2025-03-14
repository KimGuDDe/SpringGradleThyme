package data.controller;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import data.dto.SawonDto;
import data.service.SawonService;
import lombok.RequiredArgsConstructor;
import naver.storage.NcpObjectStorageService;

@Controller
@RequiredArgsConstructor
public class SawonController {
	
	final SawonService sawonService;
	final NcpObjectStorageService storageService;
	
	private String imagePath = "https://kr.object.ncloudstorage.com/bitcamp-bucket-103/sawon/";
	private String bucketName = "bitcamp-bucket-103";
	
	@GetMapping({"/"})
	public String sawonMain()
	{
		return "sawon/mainpage";		
	}
	
	@GetMapping({"/list"})
	public String sawonList(Model model)
	{
		List<SawonDto> list= sawonService.getSelectAllSawon();
		model.addAttribute("list", list);
		model.addAttribute("totalCount", list.size());
		model.addAttribute("imagePath", imagePath);
		
		return "sawon/sawonlist";		
	}
	
	@PostMapping("/insert")
	public String sawonInsert(@ModelAttribute SawonDto dto,
			@RequestParam("upload") MultipartFile upload)
	{
		if(upload.getOriginalFilename().equals(""))
			dto.setPhoto(null);
		else {
			String photo = storageService.uploadFile(bucketName, "sawon", upload);
			dto.setPhoto(photo);
		}
		
		sawonService.insertSawon(dto);
		return "redirect:./list";
	}
	
	@GetMapping({"/form"})
	public String sawonForm()
	{
		return "sawon/sawonform";		
	}
	
	@GetMapping("/delete")
    public String deleteSawon(@RequestParam("num") int num) {
        sawonService.deleteSawon(num);
        return "redirect:./list";
    }
	
	@GetMapping("/detail")
    public String sawonDetail(@RequestParam("num") int num, Model model) {
        SawonDto dto = sawonService.getSawon(num);
        model.addAttribute("dto", dto);
        model.addAttribute("imagePath", imagePath);
        
        return "sawon/sawonDetail";
    }
	
	@PostMapping("/update")
    public String sawonUpdate(@ModelAttribute SawonDto dto,
            @RequestParam("upload") MultipartFile upload) {
        // 기존 데이터 조회
        SawonDto existingDto = sawonService.getSawon(dto.getNum());
        
        if (existingDto == null) {
            // 데이터가 존재하지 않을 경우 예외 처리 (예: 에러 페이지로 리다이렉트)
            return "redirect:./list"; // 또는 에러 페이지로 이동
        }
        
        // 사진 업로드 처리
        if (upload != null && !upload.getOriginalFilename().equals("")) {
            // 기존 사진 삭제 (기존 사진이 있을 경우)
            if (existingDto.getPhoto() != null) {
                storageService.deleteFile(bucketName, "sawon", existingDto.getPhoto());
            }
            String photo = storageService.uploadFile(bucketName, "sawon", upload);
            dto.setPhoto(photo);
        } else {
            // 사진이 업로드되지 않은 경우, 기존 사진 유지
            dto.setPhoto(existingDto.getPhoto());
        }
        
        sawonService.updateSawon(dto);
        return "redirect:./detail?num=" + dto.getNum();
    }
}