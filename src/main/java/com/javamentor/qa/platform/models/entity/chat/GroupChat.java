package com.javamentor.qa.platform.models.entity.chat;

import com.javamentor.qa.platform.exception.ApiRequestException;
import com.javamentor.qa.platform.models.entity.CommentType;
import com.javamentor.qa.platform.models.entity.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "group_chat")
public class GroupChat{

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @MapsId
    private Chat chat = new Chat(ChatType.GROUP);

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "groupchat_has_users",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

    @PrePersist
    private void prePersistFunction() {
        checkConstraints();
    }

    @PreUpdate
    private void preUpdateFunction() {
        checkConstraints();
    }

    private void checkConstraints() {
        if (this.chat.getChatType() != ChatType.GROUP) {
            throw new ApiRequestException("У экземпляра Chat, связанного с GroupChat, " +
                    "поле chatType должно принимать значение ChatType.GROUP");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupChat groupChat = (GroupChat) o;
        return Objects.equals(id, groupChat.id) &&
                Objects.equals(chat, groupChat.chat) &&
                Objects.equals(users, groupChat.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chat, users);
    }
}
