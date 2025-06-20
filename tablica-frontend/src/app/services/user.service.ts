import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../enviroment';
import { User } from '../models/user.model';

interface ChangeContactDataRequest {
  email?: string;
  phone?: string;
}

interface ChangeContactDataResponse {
  user: User;
}

interface GetUsernameResponse {
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/user`;

  constructor(private http: HttpClient) { }

  changeContactData(userId: string, data: ChangeContactDataRequest): Observable<ChangeContactDataResponse> {
    return this.http.put<ChangeContactDataResponse>(`${this.apiUrl}/${userId}/contactData`, data);
  }

  getUserNameById(userId: string): Observable<GetUsernameResponse> {
    return this.http.get<GetUsernameResponse>(`${this.apiUrl}/${userId}/username`);
  }
}
