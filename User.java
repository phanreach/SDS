public class User {
    private String userEmail;
    private String password;
  
    public User(String userEmail, String password) {
        this.userEmail = userEmail;
        this.password = password;
    }
  
    public String getUseremail() {
        return userEmail;
    }
  
    public String getPassword() {
        return password;
    }
  }
  