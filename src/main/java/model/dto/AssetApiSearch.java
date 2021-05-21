package model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.dto.coincap_api.AssetCoincapDTO;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AssetApiSearch {
    private List<AssetCoincapDTO> data;
    private long timestamp;
}
