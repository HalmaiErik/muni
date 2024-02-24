import { darkScrollbar } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider, createTheme, responsiveFontSizes } from '@mui/material/styles';
import { QueryClient, QueryClientProvider } from 'react-query';
import styles from './App.module.css';
import Navbar from './components/navbar/Navbar';
import { AuthProvider } from './contexts/AuthContext';
import PageRouter from './pages/page-router/PageRouter';

const darkTheme = createTheme({
  palette: {
    mode: 'dark'
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: (theme) => ({
        '*': theme.palette.mode === 'dark' ? darkScrollbar() : null
      })
    }
  }
});

const theme = responsiveFontSizes(darkTheme);
const queryClient = new QueryClient();

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <Navbar />
          <div className={styles.pageContent}>
            <PageRouter />
          </div>
        </AuthProvider>
      </QueryClientProvider>
    </ThemeProvider>
  );
}

export default App;
