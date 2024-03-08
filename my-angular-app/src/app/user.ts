// user.model.ts
export interface User {
    id: number;
    deviceId: string;
    epochData: number;
    epochStored: number;
    latitude: number;
    longitude: number;
    delta_distance: number;
    delta_t : number;
    speed: number;
    mov_avg_spd: number;
    isActive?: boolean; 

  }
 export interface ApiResponse {
    content: User[];
    totalPages: number;
    currentPage: number;
  }