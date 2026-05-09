package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Getter;
import rozkladbot.json.deserializers.PairLinksGroupDeserializer;

@Getter
@Entity
@Table(name = "pair_links")
public class PairLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @JsonProperty(value = "link")
    private String link;
    @Column
    @JsonProperty(value = "type")
    private String type;
    @Column(nullable = false)
    @JsonProperty(value = "name")
    private String name;
    @Column(name = "group_id", nullable = false)
    @JsonProperty("groupName")
    @JsonDeserialize(using = PairLinksGroupDeserializer.class)
    private long groupId;

    @Override
    public String toString() {
        return "PairLink{" +
               "id=" + id +
               ", link='" + link + '\'' +
               ", type='" + type + '\'' +
               ", name='" + name + '\'' +
               '}';
    }

  //    @Override
//    public String toString() {
//        return "Id: %d, name: %s [%s], link: %s%n".formatted(id, name, type, link);
//    }
}
