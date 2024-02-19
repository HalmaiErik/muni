import { Alert, Button, Stack, TextField, Typography } from "@mui/material";
import { useRef, useState } from "react";
import { useAuth } from "../../contexts/AuthContext";

const Login = () => {
    const [error, setError] = useState<string>("");
    const [loading, setLoading] = useState<boolean>(false);

    const emailRef = useRef<HTMLInputElement>();
    const passwordRef = useRef<HTMLInputElement>();

    const { login } = useAuth();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        setError("");

        if (!emailRef.current?.value || !passwordRef.current?.value) {
            return setError("Please enter login details");
        }

        try {
            setLoading(true);
            await login(emailRef.current.value, passwordRef.current.value);
        } catch {
            setError("Could not login");
        }

        setLoading(false);
    }

    return (
        <form onSubmit={handleSubmit}>
            <Stack spacing={"16px"}>
                <Typography variant="h4" textAlign={"center"}>
                    Login
                </Typography>

                {error && <Alert severity="error">{error}</Alert>}

                <TextField
                    name="email"
                    label="Email"
                    type="email"
                    inputRef={emailRef} />
                <TextField
                    name="password"
                    label="Password"
                    type="password"
                    inputRef={passwordRef} />
                <Button type="submit" variant="outlined" disabled={loading}>Sign in</Button>
            </Stack>
        </form>
    );
}

export default Login;