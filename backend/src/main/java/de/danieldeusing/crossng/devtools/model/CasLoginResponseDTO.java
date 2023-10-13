package de.danieldeusing.crossng.devtools.model;

public class CasLoginResponseDTO
{
    private String token;

    public CasLoginResponseDTO(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}
