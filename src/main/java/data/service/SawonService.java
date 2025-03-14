package data.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import data.dto.SawonDto;
import data.mapper.SawonMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SawonService {
    private SawonMapper sawonMapper;
    
    public void insertSawon(SawonDto dto) {
        sawonMapper.insertSawon(dto);
    }
    
    public List<SawonDto> getSelectAllSawon() {
        return sawonMapper.getSelectAllSawon();
    }
    
    public SawonDto getSawon(int num) {
        SawonDto dto = sawonMapper.getSawon(num);
        
        if (dto.getIpsaday() != null && !dto.getIpsaday().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate ipsaDate = LocalDate.parse(dto.getIpsaday(), formatter);
                dto.setIpsaday(ipsaDate.format(formatter)); // 포맷팅된 String으로 저장
            } catch (Exception e) {
                System.err.println("ipsaday 포맷팅 오류: " + e.getMessage());
            }
        }
        
        return dto;
    }
    
    public void deleteSawon(int num) {
        sawonMapper.deleteSawon(num);
    }
    
    public void updateSawon(SawonDto dto) {
    	
        sawonMapper.updateSawon(dto);
    }
}