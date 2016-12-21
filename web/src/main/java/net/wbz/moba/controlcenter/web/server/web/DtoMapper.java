package net.wbz.moba.controlcenter.web.server.web;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.googlecode.jmapper.JMapper;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Daniel Tuerk
 */
public class DtoMapper<Target extends AbstractDto, Source extends AbstractEntity> {
    private final JMapper<AbstractDto, AbstractEntity> mapper;

    public DtoMapper() {
        mapper = new JMapper<>(AbstractDto.class, AbstractEntity.class);
    }

    public List<Target> transform(List<Source> source) {
        return Lists.transform(Lists.newArrayList(source), new Function<Source, Target>() {
            @Nullable
            @Override
            public Target apply(@Nullable Source constructionEntity) {
                return transform(constructionEntity);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Target transform(Source constructionEntity) {
        return (Target) mapper.getDestination(constructionEntity);
    }

}
