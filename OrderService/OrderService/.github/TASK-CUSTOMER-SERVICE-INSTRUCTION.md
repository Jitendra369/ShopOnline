# Purpose : Generate the code to Save the Order details 

user instruction file : .github/CODING-STANDARD-INST.md from this file 
### Order Entity :
- Order class should have 
  - orderId : string [ auto-generated ] 
  - totalAmount : BigDecimal
  - status  : OrderStatus [ enum ] > CREATED 
  - orderDate : LocalDateTime

    @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();
  - ## OrderItem Entity 
    - order : Order 
    - productId : String 
    - quantity : Integer 
    - unitPrice : BigDecimal 

    public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
    }

- generate the code crud operations on Order and OrderItem Entity 

- create a log of what u have dome in file [ .github/MEMORY.MD ], and use this for feature reference
