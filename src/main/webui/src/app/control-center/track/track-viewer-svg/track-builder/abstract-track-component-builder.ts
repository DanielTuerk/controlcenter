import {AbstractTrackPart, TrackPartStateEvent} from "../../../../../shared/openapi-gen";

export abstract class AbstractTrackComponentBuilder<
  T extends AbstractTrackPart,
  E extends TrackPartStateEvent
> {
  private static SVG_NS = 'http://www.w3.org/2000/svg';

  public static TILE = 26;
  protected static BASE_HEIGHT = 8;
  protected static BASE_COLOR = 'white';

  abstract doBuild(trackPart: T, baseX: number, baseY: number, event: E | null): Element;

  build(trackPart: T, event: E | null = null): Element {
    if (!trackPart.gridPosition || (trackPart.gridPosition.x == null || trackPart.gridPosition.y == null)) {
      throw new Error("no grid position for track part: " + JSON.stringify(trackPart));
    }
    const baseX = trackPart.gridPosition.x * AbstractTrackComponentBuilder.TILE;
    const baseY = trackPart.gridPosition.y * AbstractTrackComponentBuilder.TILE
      + ((AbstractTrackComponentBuilder.TILE - AbstractTrackComponentBuilder.BASE_HEIGHT) / 2);

    // react to complete tile for the click events
    const group = this.createElement('g');

    let tileRect = this.createInvisibleTileRect(baseX, baseY);
    group.appendChild(tileRect);
    let element = this.doBuild(trackPart, baseX, baseY, event);
    group.appendChild(element);
    return group;
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

  protected createElement(qualifiedName: string):SVGElement {
    return <SVGElement>document.createElementNS(AbstractTrackComponentBuilder.SVG_NS, qualifiedName);
  }

  private createInvisibleTileRect(baseX: number, baseY: number) {
    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;
    const hitSize = AbstractTrackComponentBuilder.TILE - 2;
    const rect = this.createElement('rect');
    rect.setAttribute('tileContainer', 'tileContainer');
    // set x/y to center of tile
    rect.setAttribute('width', String(hitSize));
    rect.setAttribute('height', String(hitSize));
    rect.setAttribute('x', String(cx - hitSize / 2));
    rect.setAttribute('y', String(cy - hitSize / 2));
    rect.setAttribute('fill', 'transparent');
    rect.style.pointerEvents = 'all';
    rect.style.cursor = 'pointer';
    return rect;
  }
}
