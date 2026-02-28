import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {DIRECTION, FUNCTION, Signal, SignalFunctionStateEvent, TYPE3} from "../../../../../../shared/openapi-gen";
import {StraightBuilder} from "./straight";

export class SignalBuilder extends StraightBuilder {

  override doBuild(signalTrackPart: Signal, baseX: number, baseY: number, event: SignalFunctionStateEvent | null = null): Element {
    let straight = super.doBuild(signalTrackPart, baseX, baseY, event);

    const group = this.createElement('g');
    group.appendChild(straight);

    const signalGroup = this.createElement('g');

    group.appendChild(signalGroup);

    const cx = baseX + AbstractTrackComponentBuilder.TILE / 2;
    const cy = baseY + AbstractTrackComponentBuilder.BASE_HEIGHT / 2;

    const hitSize = AbstractTrackComponentBuilder.TILE - 6;
    let plate = this.baseRect(cx - hitSize / 2, cy - hitSize / 2, hitSize, hitSize, 'gray', null);
    signalGroup.appendChild(plate);

    const lights: SVGElement[] = (() => {
      const pixelShift = 4.5;
      switch (signalTrackPart.type) {
        case TYPE3.Block:
          return this.createLightsForBlock(event, cx, pixelShift, cy);
        case TYPE3.Enter:
          return this.createLightsForEnter(event, cx, pixelShift, cy);
        case TYPE3.Exit:
          return this.createLightsForExit(event, cx, pixelShift, cy);
        case TYPE3.Before:
          // TODO
          return [this.createYellowLight(cx - pixelShift, cy), this.createGreenLight(cx + pixelShift, cy)];
        default:
          throw Error(`invalid signal type: ${signalTrackPart.type}`)
      }
    })();
    lights.forEach(light => signalGroup.appendChild(light));

    let degree;
    switch (signalTrackPart.direction) {
      case DIRECTION.Horizontal:
        degree = 0;
        break;
      case DIRECTION.Vertical:
        degree = 90;
        break;
    }
    signalGroup.setAttribute('transform', `rotate(${degree} ${cx} ${cy})`);
    return group;
  }

  private createLightsForBlock(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [this.createRedLight(cx - pixelShift, cy, false), this.createGreenLight(cx + pixelShift, cy, false)];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [this.createRedLight(cx - pixelShift, cy, true), this.createGreenLight(cx + pixelShift, cy, false)];
      case FUNCTION.Hp1:
        return [this.createRedLight(cx - pixelShift, cy, false), this.createGreenLight(cx + pixelShift, cy, true)];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }
  }

  private createLightsForEnter(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.createRedLight(cx + pixelShift, cy + pixelShift),
        this.createGreenLight(cx - pixelShift, cy - pixelShift),
        this.createYellowLight(cx + pixelShift, cy - pixelShift)
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.createRedLight(cx + pixelShift, cy + pixelShift, true),
          this.createGreenLight(cx - pixelShift, cy - pixelShift),
          this.createYellowLight(cx + pixelShift, cy - pixelShift)
        ];
      case FUNCTION.Hp1:
        return [
          this.createRedLight(cx + pixelShift, cy + pixelShift),
          this.createGreenLight(cx - pixelShift, cy - pixelShift, true),
          this.createYellowLight(cx + pixelShift, cy - pixelShift)
        ];
      case FUNCTION.Hp2:
        return [
          this.createRedLight(cx + pixelShift, cy + pixelShift),
          this.createGreenLight(cx - pixelShift, cy - pixelShift, true),
          this.createYellowLight(cx + pixelShift, cy - pixelShift, true)
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }
  }

  private createLightsForExit(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.createGreenLight(cx + pixelShift, cy - pixelShift),
        this.createRedLight(cx - pixelShift, cy - pixelShift),
        this.createRedLight(cx - pixelShift, cy + pixelShift)
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.createGreenLight(cx + pixelShift, cy - pixelShift),
          this.createRedLight(cx - pixelShift, cy - pixelShift, true),
          this.createRedLight(cx - pixelShift, cy + pixelShift, true),
        ];
      case FUNCTION.Hp1:
        return [
          this.createGreenLight(cx + pixelShift, cy - pixelShift, true),
          this.createRedLight(cx - pixelShift, cy - pixelShift),
          this.createRedLight(cx - pixelShift, cy + pixelShift),
        ];
      case FUNCTION.Hp2:
        return [
          this.createGreenLight(cx + pixelShift, cy - pixelShift, true),
          this.createYellowLight(cx - pixelShift, cy + pixelShift, true)
        ];
      case FUNCTION.Hp0Sh1:
        return [
          this.createRedLight(cx - pixelShift, cy - pixelShift, true),
          this.createRedLight(cx - pixelShift, cy + pixelShift),
          this.createWhiteLight(cx + pixelShift, cy - pixelShift, true)
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }

  }

  private createYellowLight(cx: number, cy: number, isOn: boolean = false) {
    return this.createLight(cx, cy, '#f8ef11', '#676608', isOn);
  }

  private createGreenLight(cx: number, cy: number, isOn: boolean = false) {
    return this.createLight(cx, cy, '#86ff38', '#224717', isOn);
  }

  private createRedLight(cx: number, cy: number, isOn: boolean = false) {
    return this.createLight(cx, cy, '#ff0012', 'rgba(97,3,19,0.56)', isOn);
  }

  private createWhiteLight(cx: number, cy: number, isOn: boolean = false) {
    return this.createLight(cx, cy, '#ffffff', '#7c7878', isOn);
  }

  private createLight(cx: number, cy: number, onColor: string, offColor: string, isOn: boolean) {
    const circle = this.createElement('circle');
    circle.setAttribute('cx', String(cx));
    circle.setAttribute('cy', String(cy));
    circle.setAttribute('r', String(4));
    circle.setAttribute('fill', isOn ? onColor : offColor);
    circle.setAttribute('stroke', 'black');
    circle.setAttribute('stroke-width', '0.5');
    return circle;
  }

// private calcDegreeValue(trackPart: Signal, isLeft: boolean) {
  //   switch (trackPart.currentPresentation) {
  //     case PRESENTATION.LeftToRight:
  //       return isLeft ? 180 : 0;
  //     case PRESENTATION.RightToLeft:
  //       return isLeft ? 0 : 180;
  //     case PRESENTATION.BottomToTop:
  //       return isLeft ? 270 : 90;
  //     case PRESENTATION.TopToBottom:
  //       return isLeft ? 90 : 270;
  //     default:
  //       throw Error(`invalid presentation value: ${trackPart.currentPresentation}`);
  //   }
  // }
}
