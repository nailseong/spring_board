package toyproject.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@SuperBuilder
public class BasicResponseDto {

    private HttpStatus httpStatus;
    private String message;

}
