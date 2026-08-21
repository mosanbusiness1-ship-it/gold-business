package com.mo.core.documents.products;

import com.mo.core.enums.FashionType;
import com.mo.core.enums.SizeSystem;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;
import java.util.List;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings/fashion-product.json")
@Document(indexName = "fashion_product_documents")
public class FashionProductDocument extends AbstractProductDocument {

    @Field(type = FieldType.Keyword, name = "brand")
    private String brand;

    @Field(type = FieldType.Keyword, name = "color")
    private String color;

    @Field(type = FieldType.Keyword, name = "material")
    private String material;

    @Field(type = FieldType.Keyword, name = "target_gender")
    private String targetGender;

    @Field(type = FieldType.Keyword, name = "size")
    private String size;

    @Field(type = FieldType.Keyword, name = "size_system")
    private SizeSystem sizeSystem;

    @Field(type = FieldType.Keyword, name = "fashion_type")
    private FashionType fashionType;

    @Field(type = FieldType.Keyword, name = "condition")
    private String condition;

    @Field(type = FieldType.Keyword, name = "sustainable_certifications")
    private List<String> sustainableCertifications;

    @Field(type = FieldType.Keyword, name = "size_fit")
    private String sizeFit;

    @Field(type = FieldType.Keyword, name = "material_origin")
    private String materialOrigin;

    @Field(type = FieldType.Text, analyzer = "standard", name = "all_text")
    private String allText;
}
