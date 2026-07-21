import {ChangeDetectionStrategy, Component, effect, inject, input} from '@angular/core';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {debounceTime, distinctUntilChanged, switchMap, take} from 'rxjs/operators';
import {ConfigService} from "../../../shared/config.service";
import {MatFormField, MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";
import {MatMenu, MatMenuTrigger} from "@angular/material/menu";

@Component({
  selector: 'app-config-input',
  standalone: true,
  imports: [ReactiveFormsModule, MatInput, MatFormField, MatIcon, MatIconButton, MatMenu, MatMenuTrigger],
  changeDetection: ChangeDetectionStrategy.Eager,
  templateUrl: './config-input.component.html',
  styleUrl: './config-input.component.css'
})
export class ConfigInputComponent {
  private configService = inject(ConfigService);

  configKey = input.required<string>();
  label = input('');
  hint = input('');
  placeholder = input('');
  debounceMs = input(500);
  type = input<'text' | 'number'>('text');

  control = new FormControl('', {nonNullable: true});
  loading = false;
  saving = false;

  constructor() {
    effect(() => {
      const key = this.configKey();
      if (!key) return;
      this.loading = true;
      this.configService.loadConfigValue(key)
        .pipe(take(1))
        .subscribe(value => {
          this.control.setValue(value ?? '', {emitEvent: false});
          this.loading = false;
        });
    });

    this.control.valueChanges
      .pipe(
        debounceTime(this.debounceMs()),
        distinctUntilChanged(),
        switchMap(value => {
          this.saving = true;
          return this.configService.saveConfigValue(this.configKey(), value);
        }),
        takeUntilDestroyed()
      )
      .subscribe({
        next: () => { this.saving = false; },
        error: () => { this.saving = false; }
      });
  }
}
