## coding standard 

- folder structure 
  - their should be separate folder for each 
    - Entity > it should be extend with AbstractBaseEntity contains id(UUID), createdAt, updatedAt
    - DTO 
    - Service > create service and its Implemented class 
    - Repository > User JPA entity 
    - Util
    - Configuration 
    - Controller
- User Lombok annotation when requried , like @Getter , @Setter, @RequriedArgsConstructor... like
- Exception Handling 
  - use proper exception handling , create the proper custom Exception class to handle the exception 
  - user Optional when null is expected form the response 