import {Component, DestroyRef, inject, Input, OnChanges, OnInit, SimpleChanges, ChangeDetectionStrategy} from '@angular/core';

import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {debounceTime, distinctUntilChanged, switchMap, take} from 'rxjs/operators';
import {ConfigService} from "../../../shared/config.service";

@Component({
  selector: 'app-config-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './config-input.component.html'
})
export class ConfigInputComponent implements OnInit, OnChanges {
  private configService = inject(ConfigService);
  private destroyRef = inject(DestroyRef);

  @Input({required: true}) configKey!: string;
  @Input() label = '';
  @Input() placeholder = '';
  @Input() debounceMs = 500;
  @Input() type: 'text' | 'number' = 'text';

  control = new FormControl('', {nonNullable: true});

  loading = false;
  saving = false;

  ngOnInit(): void {
    this.registerAutoSave();
    this.loadValue();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['configKey'] && !changes['configKey'].firstChange) {
      this.loadValue();
    }
  }

  private loadValue(): void {
    if (!this.configKey) {
      return;
    }

    this.loading = true;

    this.configService.loadConfigValue(this.configKey)
      .pipe(take(1))
      .subscribe(value => {
        this.control.setValue(value ?? '', {emitEvent: false});
        this.loading = false;
      });
  }

  private registerAutoSave(): void {
    this.control.valueChanges
      .pipe(
        debounceTime(this.debounceMs),
        distinctUntilChanged(),
        switchMap(value => {
          this.saving = true;
          return this.configService.saveConfigValue(this.configKey, value);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: () => {
          this.saving = false;
        },
        error: () => {
          this.saving = false;
        }
      });
  }
}
