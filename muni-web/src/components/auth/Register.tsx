import { Alert, Button, Stack, TextField, Typography } from "@mui/material";
import { useRef, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";

const Register = () => {
    const [error, setError] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    const emailRef = useRef<HTMLInputElement>();
    const passwordRef = useRef<HTMLInputElement>();
    const reenterPasswordRef = useRef<HTMLInputElement>();

    const { signup } = useAuth();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError("");

        if (passwordRef.current?.value !== reenterPasswordRef.current?.value) {
            return setError("Passwords do not match");
        }

        try {
            setLoading(true);
            if (emailRef.current && passwordRef.current) {
                await signup(emailRef.current.value, passwordRef.current.value);
            }
        } catch {
            setError("Could not create account");
        }

        setLoading(false);
    }

    return (
        <Stack spacing={"16px"}>
            <Typography variant="h4" textAlign={"center"}>
                    Create account
            </Typography>

            {error && <Alert severity="error">{error}</Alert>}

            <form onSubmit={handleSubmit}>
                <Stack spacing={"16px"}>
                    <TextField 
                        name="email" 
                        label="Email" 
                        type="email"
                        inputRef={emailRef}
                        required />
                    <TextField 
                        name="password" 
                        label="Password" 
                        type="password" 
                        inputRef={passwordRef}
                        required />
                    <TextField 
                        label="Re-enter password" 
                        type="password" 
                        inputRef={reenterPasswordRef}
                        required />
                    <Button type="submit" variant="outlined" disabled={loading}>Sign up</Button>
                </Stack>
            </form>
        </Stack>
    );
}

export default Register;