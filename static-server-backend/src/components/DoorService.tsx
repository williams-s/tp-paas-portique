export class DoorService {
  static async openDoor(doorId: string): Promise<boolean> {
    try {
      const response = await fetch('/entrance/api/door/open/' + doorId, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
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