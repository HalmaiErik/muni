import { useState } from "react";
import { Paper, Stack, ToggleButton, ToggleButtonGroup } from "@mui/material";
import styles from "./Auth.module.css"
import Login from "./Login";
import Register from "./Register";

const Auth = () => {
    const [authMethod, setAuthMethod] = useState<string>("login");

    const changeAuthMethod = (event: React.MouseEvent<HTMLElement>, newAuthMethod: string | null) => {
        if (newAuthMethod) {
            setAuthMethod(newAuthMethod);
        }
    }

    return (
        <Stack spacing={"24px"}>
            <ToggleButtonGroup
                value={authMethod}
                color="primary"
                exclusive
                onChange={changeAuthMethod}
                className={styles.authMethodToggle}
                fullWidth
            >
                <ToggleButton value="login">Sign in</ToggleButton>
                <ToggleButton value="register">Sign up</ToggleButton>
            </ToggleButtonGroup>

            {authMethod === "login" && <Login />}
            {authMethod === "register" && <Register />}
        </Stack>
    );
}

export default Auth;