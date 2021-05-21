package model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.dto.coincap_api.PriceAtGivenTime;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistoryApiSearch {

    public List<PriceAtGivenTime> data;
    public long timestamp;

}
