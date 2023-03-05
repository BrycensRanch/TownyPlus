package me.romvnly.TownyPlus.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class SavedCode {
    private String code;
    private String createdBy;
    private Date createdOn;
}
