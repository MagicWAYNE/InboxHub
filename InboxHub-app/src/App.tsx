import { Assets as NavigationAssets } from '@react-navigation/elements';
import { DarkTheme, DefaultTheme } from '@react-navigation/native';
import { Asset } from 'expo-asset';
import { createURL } from 'expo-linking';
import * as SplashScreen from 'expo-splash-screen';
import * as React from 'react';
import { useColorScheme, View, ActivityIndicator } from 'react-native';
import { Navigation } from './navigation';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { AuthScreen } from './screens/AuthScreen';
import { ConfigurationError } from './components/common/ConfigurationError';

Asset.loadAsync([
  ...NavigationAssets,
  require('./assets/newspaper.png'),
  require('./assets/bell.png'),
]);

SplashScreen.preventAutoHideAsync();

const prefix = createURL('/');

function AppContent() {
  const { user, loading } = useAuth();
  const colorScheme = useColorScheme();
  const theme = colorScheme === 'dark' ? DarkTheme : DefaultTheme;

  React.useEffect(() => {
    if (!loading) {
      SplashScreen.hideAsync();
    }
  }, [loading]);

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
        <ActivityIndicator size="large" color="#007AFF" />
      </View>
    );
  }

  if (!user) {
    return <AuthScreen />;
  }

  return (
    <Navigation
      theme={theme}
      linking={{
        enabled: 'auto',
        prefixes: [prefix],
      }}
    />
  );
}

export function App() {
  const [configError, setConfigError] = React.useState<Error | null>(null);

  React.useEffect(() => {
    // Test Supabase configuration on app start
    try {
      require('./lib/supabase'); // This will throw if configuration is invalid
    } catch (error) {
      setConfigError(error as Error);
    }
  }, []);

  const handleRetry = () => {
    setConfigError(null);
    // Force re-import of supabase module
    delete require.cache[require.resolve('./lib/supabase')];
    try {
      require('./lib/supabase');
      // If successful, the error will be cleared and app will reload
    } catch (error) {
      setConfigError(error as Error);
    }
  };

  if (configError) {
    return <ConfigurationError error={configError} onRetry={handleRetry} />;
  }

  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}
