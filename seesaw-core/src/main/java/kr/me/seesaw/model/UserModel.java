package kr.me.seesaw.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Schema(name = "UserModel", description = "계정 모델")
@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
@AllArgsConstructor
public final class UserModel extends BaseModel {

}
