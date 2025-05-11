export interface UserCreationRequestDto {
    username: string
    email: string
    password: string
    confirmPassword: string
    role: 'producer' | 'customer'
    avatar?: File
}