import {AbstractTrackPart} from "../../../../../shared/openapi-gen";

export abstract class AbstractTrackComponentBuilder {
  private static SVG_NS = 'http://www.w3.org/2000/svg';

  public static TILE = 25;
  protected static BASE_HEIGHT = 7;

  abstract doBuild<T extends AbstractTrackPart>(trackPart: T, baseX: number, baseY: number): Element;

  build<T extends AbstractTrackPart>(trackPart: T): Element {
    if (!trackPart.gridPosition || (trackPart.gridPosition.x == null || trackPart.gridPosition.y == null)) {
      throw new Error("no grid position for track part: " + JSON.stringify(trackPart));
    }
    const baseX = trackPart.gridPosition.x * AbstractTrackComponentBuilder.TILE;
    const baseY = trackPart.gridPosition.y * AbstractTrackComponentBuilder.TILE
      + ((AbstractTrackComponentBuilder.TILE - AbstractTrackComponentBuilder.BASE_HEIGHT) / 2);

    return this.doBuild(trackPart, baseX, baseY);
  }

  protected baseRect(x: number, y: number, width: number, height: number, fill: string, groupTransform: string | null): Element {
    const rect = this.createElement('rect');
    rect.setAttribute('x', `${x}`);
    rect.setAttribute('y', `${y}`);
    rect.setAttribute('width', `${width}`);
    rect.setAttribute('height', `${height}`);
    rect.setAttribute('fill', `${fill}`);
    if (groupTransform != null) {
      rect.setAttribute('transform', `${groupTransform}`);
    }
    return rect;
  }

  protected createElement(qualifiedName: string) {
    return document.createElementNS(AbstractTrackComponentBuilder.SVG_NS, qualifiedName);
  }
}
