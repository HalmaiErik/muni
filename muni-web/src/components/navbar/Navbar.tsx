import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import AccountCircle from '@mui/icons-material/AccountCircle';
import { AppBar, Button, IconButton, Menu, MenuItem, Toolbar } from "@mui/material";
import { useState } from "react";
import { useNavigate } from 'react-router-dom';
import { useAuth } from "../../contexts/AuthContext";

const Navbar = () => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const { currentUser, logout } = useAuth();
  const navigate = useNavigate();

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
      <Toolbar sx={{ alignItems: 'center' }}>
        <div style={{ flexGrow: 1 }}>
          <Button sx={{
            fontFamily: 'monospace', fontWeight: 700, fontSize: 20, letterSpacing: '.3rem', "&.MuiButtonBase-root:hover": { bgcolor: "transparent" }
          }}
            onClick={() => navigate('/')} color='inherit' >
            Muni
          </Button>
          <Button sx={{ marginLeft: '24px' }} startIcon={<AccountBalanceWalletIcon />} color='inherit' onClick={() => navigate('/accounts')}>Accounts</Button>
        </div>
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