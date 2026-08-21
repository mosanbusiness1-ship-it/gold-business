package com.mo.core.documents.needs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.mo.core.documents.products.AbstractProductDocument;
import com.mo.core.documents.products.RealEstateProductDocument;
import com.mo.core.enums.ElectronicType;
import com.mo.core.enums.RealEstateType;

// Aucun besoin d'importer UserNeedVisitor ici si tu ne l'utilises pas directement

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "electronic_need_documents") 
public class ElectronicNeedDocument extends AbstractUserNeedDocument {

    @Field(type = FieldType.Keyword, name = "electronic_type")
    private ElectronicType electronicType;

    @Field(type = FieldType.Text, name = "brand")
    private String brand;

    @Field(type = FieldType.Text, name = "model")
    private String model;

    @Field(type = FieldType.Text, name = "specifications")
    private String specifications;

    @Field(type = FieldType.Keyword, name = "warranty_period")
    private String warrantyPeriod;

    @Field(type = FieldType.Integer, name = "min_storage_gb")
    private Integer minStorageGB;

    @Field(type = FieldType.Integer, name = "min_ram_gb")
    private Integer minRAMGB;

    @Field(type = FieldType.Keyword, name = "preferred_os")
    private String preferredOS;

    @Field(type = FieldType.Integer, name = "max_age_years")
    private Integer maxAgeYears;

    @Field(type = FieldType.Boolean, name = "warranty_required")
    private Boolean warrantyRequired;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
