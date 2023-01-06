package com.doohee.mediaserver.repository.support;

import com.doohee.mediaserver.dto.QVideoAbstractDto;
import com.doohee.mediaserver.dto.VideoAbstractDto;
import com.doohee.mediaserver.entity.Exposure;
import com.doohee.mediaserver.entity.Video;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;


import java.util.List;

import static com.doohee.mediaserver.entity.QVideo.video;

@Repository
public class VideoRepositorySupport extends QuerydslRepositorySupport {
    @Autowired
    private JPAQueryFactory jpaQueryFactory;
    @Autowired
    Environment environment;
    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     */
    public VideoRepositorySupport() {
        super(Video.class);
    }

    public Page<VideoAbstractDto> findVideo(String userId, String uploaderId, String keyword, Pageable pageable){
        JPAQuery<VideoAbstractDto> query = jpaQueryFactory.select(new QVideoAbstractDto(video.videoId, video.title, video.thumbnailExtension, video.uploader.username, video.uploadedDate))
                .from(video)
                .where(uploader(uploaderId), videoPermission(userId), keyword(keyword))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        query.orderBy(video.uploadedDate.desc());
        List<VideoAbstractDto> results = query.fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    private BooleanExpression uploader(String userId){
        return TextUtils.isEmpty(userId)? null
                : video.uploader.username.eq(userId);
    }
    private BooleanExpression videoPermission(String userId){
        return TextUtils.isEmpty(userId)? video.exposure.eq(Exposure.PUBLIC)
                :video.exposure.eq(Exposure.PUBLIC)
                .or(video.uploader.username.eq(userId))
                .or(video.userVideoRelations.any().user.username.eq(userId));
    }

    private BooleanExpression keyword(String keyword){
        return TextUtils.isEmpty(keyword)?null
                : video.title.contains(keyword)
                .or(video.description.contains(keyword));
    }

}
