import { Button } from "@mui/material";
import { useAuth } from "../../contexts/AuthContext";
import { useState } from "react";

const LogoutButton = () => {
    const [loading, setLoading] = useState<boolean>(false);

    const { logout } = useAuth();

    const handleClick = async () => {
        setLoading(true);
        await logout();
        setLoading(false);
    }

    return (
        <Button onClick={handleClick} variant="outlined" disabled={loading}>Logout</Button>
    );
}

export default LogoutButton;