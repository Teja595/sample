// user.model.ts
export interface User {
    id: number;
    deviceId: string;
    epochData: number;
    epochStored: number;
    latitude: number;
    longitude: number;
    isActive?: boolean; 
  }
 export interface ApiResponse {
    content: User[];
    totalPages: number;
    currentPage: number;
  }