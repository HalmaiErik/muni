import { AppBar, IconButton, Menu, MenuItem, Toolbar, Typography } from "@mui/material";
import AccountCircle from '@mui/icons-material/AccountCircle';
import { useState } from "react";
import { useAuth } from "../../contexts/AuthContext";
import styles from "./Navbar.module.css"

const Navbar = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const { currentUser, logout } = useAuth();

  const logoutClick = async () => {
    setLoading(true);
    await logout();
    setLoading(false);
    handleClose();
  };

  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Typography className={styles.appTitle} variant="h6" component="div">
          Muni
        </Typography>
        {currentUser && (
          <div>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleMenu}
              color="inherit"
            >
              <AccountCircle />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorEl}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
              open={Boolean(anchorEl)}
              onClose={handleClose}
            >
              <MenuItem onClick={logoutClick} disabled={loading}>Logout</MenuItem>
            </Menu>
          </div>
        )}
      </Toolbar>
    </AppBar >
  );
}

export default Navbar;