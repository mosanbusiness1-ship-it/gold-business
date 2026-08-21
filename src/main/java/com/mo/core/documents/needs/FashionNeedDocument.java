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

import com.mo.core.enums.FashionType;
import com.mo.core.enums.SizeSystem;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "fashion_need_documents")
public class FashionNeedDocument extends AbstractUserNeedDocument {

    @Field(name = "brand", type = FieldType.Keyword)
    private String brand;

    @Field(name = "color", type = FieldType.Keyword)
    private String color;

    @Field(name = "material", type = FieldType.Keyword)
    private String material;

    @Field(name = "target_gender", type = FieldType.Keyword)
    private String targetGender;

    @Field(name = "size", type = FieldType.Keyword)
    private String size;

    @Field(name = "size_system", type = FieldType.Keyword)
    private SizeSystem sizeSystem;

    @Field(name = "fashion_type", type = FieldType.Keyword)
    private FashionType fashionType;

    @Field(name = "preferred_brands", type = FieldType.Keyword)
    private java.util.List<String> preferredBrands;

    @Field(name = "fit_preference", type = FieldType.Keyword)
    private String fitPreference;

    @Field(name = "material_preference", type = FieldType.Keyword)
    private String materialPreference;

    @Field(name = "style_tags", type = FieldType.Keyword)
    private java.util.List<String> styleTags;

    @Field(name = "gender_neutral", type = FieldType.Boolean)
    private Boolean genderNeutral;

    @Field(name = "all_text", type = FieldType.Text, analyzer = "standard")
    private String allText;
}


