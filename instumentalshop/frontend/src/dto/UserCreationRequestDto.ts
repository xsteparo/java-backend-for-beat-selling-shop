export interface UserCreationRequestDto {
    username: string
    email: string
    password: string
    confirmPassword: string
    role: 'producer' | 'artist'
    avatar?: File
}