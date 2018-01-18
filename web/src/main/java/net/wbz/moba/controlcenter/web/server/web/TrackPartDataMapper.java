package net.wbz.moba.controlcenter.web.server.web;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Singleton;

import net.wbz.moba.controlcenter.web.server.persist.construction.track.AbstractTrackPartEntity;
import net.wbz.moba.controlcenter.web.shared.track.model.AbstractTrackPart;

/**
 * TODO usage of generics
 * <p/>
 * Mapper for {@link AbstractTrackPartEntity} implementations to generate the corresponding implementation of the
 * {@link AbstractTrackPart} which is defined by {@link AbstractTrackPartEntity#getDefaultDtoClass()}.
 *
 * @author Daniel Tuerk
 */
@Singleton
public class TrackPartDataMapper {

    /**
     * Mapper from entity to dto.
     */
    private final Map<Class<? extends AbstractTrackPart>, DataMapper> dtoMappers = Maps.newConcurrentMap();
    /**
     * Mapper from dto to entity.
     */
    private final Map<Class<? extends AbstractTrackPartEntity>, DataMapper> entityMappers = Maps.newConcurrentMap();

    public TrackPartDataMapper() {
        Reflections reflections = new Reflections(AbstractTrackPartEntity.class.getPackage().getName());
        Set<Class<? extends AbstractTrackPartEntity>> classes = reflections.getSubTypesOf(
                AbstractTrackPartEntity.class);
        for (Class<? extends AbstractTrackPartEntity> trackPartClazz : classes) {
            if (!Modifier.isAbstract(trackPartClazz.getModifiers())) {
                if (!entityMappers.containsKey(trackPartClazz)) {
                    try {
                        Class<? extends AbstractTrackPart> defaultDtoClass = trackPartClazz.newInstance()
                                .getDefaultDtoClass();
                        DataMapper dataMapper = new DataMapper<>(defaultDtoClass, trackPartClazz);
                        entityMappers.put(trackPartClazz, dataMapper);
                        dtoMappers.put(defaultDtoClass, dataMapper);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("can't create data mapper for track part: " + trackPartClazz
                                .getName());
                    }
                }
            }
        }

    }

    @SuppressWarnings(value = "unchecked")
    public AbstractTrackPartEntity transformTrackPart(AbstractTrackPart trackPart) {
        return (AbstractTrackPartEntity) dtoMappers.get(trackPart.getClass()).transformTarget(trackPart);
    }

    @SuppressWarnings(value = "unchecked")
    public AbstractTrackPart transformTrackPartEntity(AbstractTrackPartEntity trackPart) {
        return (AbstractTrackPart) entityMappers.get(trackPart.getClass()).transformSource(trackPart);
    }

    public Collection<AbstractTrackPart> transformTrackPartEntities(Collection<AbstractTrackPartEntity> entities) {
        return Lists.newArrayList(Iterables.transform(entities,
                new Function<AbstractTrackPartEntity, AbstractTrackPart>() {
                    @Override
                    public AbstractTrackPart apply(AbstractTrackPartEntity trackPartEntity) {
                        return transformTrackPartEntity(trackPartEntity);
                    }
                }));
    }
}
