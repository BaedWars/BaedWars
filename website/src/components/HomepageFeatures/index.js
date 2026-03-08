import clsx from 'clsx';
import styles from './styles.module.css';

const FeatureList = [
  {
    title: 'Low resource usage',
    Svg: require('@site/static/img/cpu-usage.svg').default,
    description: (
      <>
        Baedwars uses 4x less memory and 2x less CPU compared to a 1.21.11 Bedwars PaperMC server (with Bedwars plugins installed).
      </>
    ),
  },
  {
    title: 'Fully customizable',
    Svg: require('@site/static/img/tools.svg').default,
    description: (
      <>
        Configure shops, maps, and server behavior to fit your own Bedwars style, from
        casual public matches to highly competitive custom setups.
      </>
    ),
  },
  {
    title: 'Powered by Minestom',
    Svg: require('@site/static/img/minestom.svg').default,
    description: (
      <>
        Built on Minestom, a next-generation Minecraft server framework, to deliver smooth
        performance under load and faster feature iteration.
      </>
    ),
  },
];

function Feature({Svg, title, description}) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures() {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
