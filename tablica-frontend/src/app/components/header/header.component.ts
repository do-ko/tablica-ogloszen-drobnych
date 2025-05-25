import { Component, Input } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  @Input() currentUser: User | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  logout(event: Event): void {
    event.preventDefault();
    this.authService.logout();
    this.router.navigate(["/auth"]);
  }
}
