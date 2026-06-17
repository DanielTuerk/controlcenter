import {AbstractTrackComponentBuilder} from "../abstract-track-component-builder";
import {DIRECTION, FUNCTION, Signal, SignalFunctionStateEvent, SIGNALTYPE} from "../../../../../../shared/openapi-gen";
import {StraightBuilder} from "./straight";

export class SignalBuilder extends StraightBuilder<Signal> {

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
        case SIGNALTYPE.Block:
          return this.lightsForBlock(event, cx, pixelShift, cy);
        case SIGNALTYPE.Enter:
          return this.lightsForEnter(event, cx, pixelShift, cy);
        case SIGNALTYPE.Exit:
          return this.lightsForExit(event, cx, pixelShift, cy);
        case SIGNALTYPE.Before:
          return this.lightsForBefore(event, cx, pixelShift, cy);
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

  private lightsForBlock(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.red(cx - pixelShift, cy, false),
        this.green(cx + pixelShift, cy, false)
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.red(cx - pixelShift, cy, true),
          this.green(cx + pixelShift, cy, false)
        ];
      case FUNCTION.Hp1:
        return [
          this.red(cx - pixelShift, cy, false),
          this.green(cx + pixelShift, cy, true)
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }
  }

  private lightsForEnter(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.red(cx + pixelShift, cy + pixelShift),
        this.green(cx - pixelShift, cy - pixelShift),
        this.yellow(cx + pixelShift, cy - pixelShift)
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.red(cx + pixelShift, cy + pixelShift, true),
          this.green(cx - pixelShift, cy - pixelShift),
          this.yellow(cx + pixelShift, cy - pixelShift)
        ];
      case FUNCTION.Hp1:
        return [
          this.red(cx + pixelShift, cy + pixelShift),
          this.green(cx - pixelShift, cy - pixelShift, true),
          this.yellow(cx + pixelShift, cy - pixelShift)
        ];
      case FUNCTION.Hp2:
        return [
          this.red(cx + pixelShift, cy + pixelShift),
          this.green(cx - pixelShift, cy - pixelShift, true),
          this.yellow(cx + pixelShift, cy - pixelShift, true)
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }
  }

  private lightsForBefore(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.yellow(cx - pixelShift, cy+pixelShift),
        this.yellow(cx + pixelShift, cy+pixelShift),
        this.green(cx - pixelShift, cy -pixelShift),
        this.green(cx + pixelShift, cy -pixelShift),
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.yellow(cx - pixelShift, cy+pixelShift, true),
          this.yellow(cx + pixelShift, cy+pixelShift, true),
          this.green(cx - pixelShift, cy -pixelShift),
          this.green(cx + pixelShift, cy -pixelShift),
        ];
      case FUNCTION.Hp1:
        return [
          this.yellow(cx - pixelShift, cy+pixelShift),
          this.yellow(cx + pixelShift, cy+pixelShift),
          this.green(cx - pixelShift, cy -pixelShift, true),
          this.green(cx + pixelShift, cy -pixelShift, true),
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }
  }

  private lightsForExit(event: SignalFunctionStateEvent | null, cx: number, pixelShift: number, cy: number) {
    if (!event) {
      return [
        this.green(cx + pixelShift, cy - pixelShift),
        this.red(cx - pixelShift, cy - pixelShift),
        this.red(cx - pixelShift, cy + pixelShift)
      ];
    }
    switch (event?.signalFunction) {
      case FUNCTION.Hp0:
        return [
          this.green(cx + pixelShift, cy - pixelShift),
          this.red(cx - pixelShift, cy - pixelShift, true),
          this.red(cx - pixelShift, cy + pixelShift, true),
        ];
      case FUNCTION.Hp1:
        return [
          this.green(cx + pixelShift, cy - pixelShift, true),
          this.red(cx - pixelShift, cy - pixelShift),
          this.red(cx - pixelShift, cy + pixelShift),
        ];
      case FUNCTION.Hp2:
        return [
          this.green(cx + pixelShift, cy - pixelShift, true),
          this.yellow(cx - pixelShift, cy + pixelShift, true)
        ];
      case FUNCTION.Hp0Sh1:
        return [
          this.red(cx - pixelShift, cy - pixelShift, true),
          this.red(cx - pixelShift, cy + pixelShift),
          this.white(cx + pixelShift, cy - pixelShift, true)
        ];
      default:
        throw Error(`invalid signal function ${event?.signalFunction} of signal: ${event?.signalId}`);
    }

  }

  private yellow(cx: number, cy: number, isOn: boolean = false) {
    return this.light(cx, cy, '#f8ef11', '#676608', isOn);
  }

  private green(cx: number, cy: number, isOn: boolean = false) {
    return this.light(cx, cy, '#86ff38', '#224717', isOn);
  }

  private red(cx: number, cy: number, isOn: boolean = false) {
    return this.light(cx, cy, '#ff0012', 'rgba(97,3,19,0.56)', isOn);
  }

  private white(cx: number, cy: number, isOn: boolean = false) {
    return this.light(cx, cy, '#ffffff', '#7c7878', isOn);
  }

  private light(cx: number, cy: number, onColor: string, offColor: string, isOn: boolean) {
    const circle = this.createElement('circle');
    circle.setAttribute('cx', String(cx));
    circle.setAttribute('cy', String(cy));
    circle.setAttribute('r', String(4));
    circle.setAttribute('fill', isOn ? onColor : offColor);
    circle.setAttribute('stroke', 'black');
    circle.setAttribute('stroke-width', '0.5');
    return circle;
  }

}
