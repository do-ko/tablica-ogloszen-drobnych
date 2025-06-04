import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MessageBoxComponent} from './components/message-box/message-box.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MessageBoxComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'tablica-frontend';
}
