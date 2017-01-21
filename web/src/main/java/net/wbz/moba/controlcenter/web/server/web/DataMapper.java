package net.wbz.moba.controlcenter.web.server.web;

import java.util.Collection;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.jmapper.JMapper;

import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * Mapper to create DTOs from Entities and backwards.
 *
 * @author Daniel Tuerk
 */
public class DataMapper<Target extends AbstractDto, Source extends AbstractEntity> {
    private final JMapper<Target, Source> sourceMapper;
    private final JMapper<Source, Target> targetMapper;

    public DataMapper(Class<Target> dtoClass, Class<Source> entityClass) {
        sourceMapper = new JMapper<>(dtoClass, entityClass);
        targetMapper = new JMapper<>(entityClass, dtoClass);
    }

    public Collection<Target> transformSource(final Collection<Source> sourceEntities) {
        return Lists.newArrayList(Iterables.transform(sourceEntities, new Function<Source, Target>() {
            @Nullable
            @Override
            public Target apply(@Nullable Source source) {
                return transformSource(source);
            }
        }));
    }

    @SuppressWarnings("unchecked")
    public Target transformSource(Source source) {
        return (Target) sourceMapper.getDestination(source);
    }

    public Collection<Source> transformTarget(final Collection<Target> targetDtos) {
        return Lists.newArrayList(Iterables.transform(targetDtos, new Function<Target, Source>() {
            @Override
            public Source apply(Target source) {
                return transformTarget(source);
            }
        }));
    }

    @SuppressWarnings("unchecked")
    public Source transformTarget(Target target) {
        return (Source) targetMapper.getDestination(target);
    }

}
