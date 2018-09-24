package net.wbz.moba.controlcenter.web.server.web;

import com.google.common.collect.Lists;
import com.googlecode.jmapper.JMapper;
import java.util.Collection;
import java.util.stream.Collectors;
import net.wbz.moba.controlcenter.web.server.persist.AbstractEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractDto;

/**
 * Mapper to create DTOs from Entities and backwards.
 *
 * @author Daniel Tuerk
 */
public class DataMapper<Target extends AbstractDto, Source extends AbstractEntity> {

    /**
     * Mapper to transform {@link Target} to {@link Source}.
     */
    private final JMapper<Target, Source> sourceMapper;
    /**
     * Mapper to transform {@link Source} to {@link Target}.
     */
    private final JMapper<Source, Target> targetMapper;

    /**
     * Create new mapper.
     *
     * @param dtoClass dto class
     * @param entityClass entity class
     */
    public DataMapper(Class<Target> dtoClass, Class<Source> entityClass) {
        sourceMapper = new JMapper<>(dtoClass, entityClass);
        targetMapper = new JMapper<>(entityClass, dtoClass);

        configureSourceMapper(sourceMapper);
        configureTargetMapper(targetMapper);
    }

    protected void configureSourceMapper(JMapper<Target, Source> sourceMapper) {
        // optional configuration
    }

    protected void configureTargetMapper(JMapper<Source, Target> targetMapper) {
        // optional configuration
    }

    /**
     * Transform the list of {@link Source} to list of {@link Target}.
     *
     * @param sourceEntities {@link Source}s to transform
     * @return list of {@link Target} from given list of {@link Source}
     */
    public Collection<Target> transformSource(final Collection<Source> sourceEntities) {
        return Lists.newArrayList(sourceEntities.stream().map(this::transformSource).collect(Collectors.toList()));
    }

    /**
     * Transform the given {@link Source} to {@link Target}.
     *
     * @param source {@link Source} to transform
     * @return {@link Target} from given {@link Source}
     */
    @SuppressWarnings("unchecked")
    public Target transformSource(Source source) {
        return sourceMapper.getDestination(source);
    }

    /**
     * Transform the list of {@link Target} to list of {@link Source}.
     *
     * @param targetDtos {@link Target}s to transform
     * @return list of {@link Source} from given list of {@link Target}
     */
    public Collection<Source> transformTarget(final Collection<Target> targetDtos) {
        return Lists.newArrayList(targetDtos.stream().map(this::transformTarget).collect(Collectors.toList()));
    }

    /**
     * Transform the given {@link Target} to {@link Source}.
     *
     * @param target {@link Target} to transform
     * @return {@link Source} from given {@link Target}
     */
    @SuppressWarnings("unchecked")
    public Source transformTarget(Target target) {
        return targetMapper.getDestination(target);
    }

}
