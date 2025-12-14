import {Door} from "../App.tsx";

export class DoorService {
  static async openDoor(door: Door): Promise<boolean> {
    try {
      const response = await fetch('/entrance/api/door/open',{
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(door),
      });
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('Error opening door:', error);
      return false;
    }
  }
}