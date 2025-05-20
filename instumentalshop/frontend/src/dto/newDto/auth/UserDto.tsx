export interface UserDto {
    userId: number
    username: string
    email: string
    avatarUrl: string
    role: 'CUSTOMER' | 'PRODUCER' | 'ADMIN';
    registrationDate: string
    bio?: string
}