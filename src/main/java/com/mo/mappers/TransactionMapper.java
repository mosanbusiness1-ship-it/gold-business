// package com.mo.mappers;


// import java.util.UUID;
// import org.mapstruct.AfterMapping;
// import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
// import org.mapstruct.MappingTarget;
// import org.mapstruct.ReportingPolicy;

// import com.mo.dtos.CreateMyCoolPayTransactionDto;
// import com.mo.dtos.CreatePayPalTransactionDto;
// import com.mo.dtos.CreateStripeTransactionDto;
// import com.mo.dtos.TransactionDto;
// import com.mo.entities.Transaction;

// /**
//  *
//  * @author douglas
//  */
// @Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
// public interface TransactionMapper extends BaseMapper<Transaction, TransactionDto> {
        
//     public Transaction myCoolpayDtoToEntity(CreateMyCoolPayTransactionDto entity);
//     public CreateMyCoolPayTransactionDto toMyCoolPayTransactionDto(Transaction entity);
    
    
//     public Transaction stripeDtoToEntity(CreateMyCoolPayTransactionDto entity);
//     public CreateStripeTransactionDto toStripeTransactionDto(Transaction entity);
    
    
//     public Transaction payPalDtoToEntity(CreateMyCoolPayTransactionDto entity);
//     public CreatePayPalTransactionDto toPayPalTransactionDto(Transaction entity);
    

    
//     @AfterMapping
//     default void setUUID(@MappingTarget Transaction transaction) {
//         if (transaction.getId() == null) {
//         	transaction.setId(UUID.randomUUID());
//         }
//         if (transaction.getTransactionMethod()== null) {
//         	transaction.setTransactionStatus("PENDING");
//         }
		
//     }
    
// }
