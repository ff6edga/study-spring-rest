package study.spring.rest.studyspringrest.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {

	@Id
	@GeneratedValue
	private Integer id;

	//@Column(unique = true)
	private String email;

	private String password;

	//enum 수 자체가 적고, 거의 항상 쓰이므로 미리 가져오자
	@ElementCollection(fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<AccountRole> roles;
}
