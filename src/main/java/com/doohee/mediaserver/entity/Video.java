package com.doohee.mediaserver.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="VIDEO")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @Column(name="VIDEO_ID", length = 127)
    private String videoId;

    @Column
    private String title;

    @Column(length=4095)
    private String description;

    @Column
    private VideoStatus status;

    @Column
    private Exposure exposure;

    @Column
    private LocalDateTime uploadedDate;

    @Column
    private String extension;
    //media 파일의 확장자''''
    @Column
    private String thumbnailExtension;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User uploader;

    @OneToMany(mappedBy = "video")
    private List<UserVideoRelation> userVideoRelations;

    @OneToMany(mappedBy = "video")
    private List<Comment> comments;

}
